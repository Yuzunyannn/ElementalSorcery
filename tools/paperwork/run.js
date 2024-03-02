const { assert, table } = require('console')
var fs = require('fs')

//正式开始了！！~~~~~~~~~~-------------------------

class Parser {
    tid = ""
    results = []
    raws = []
    langMap = {}
    parserRuntime = {}

    constructor(type) {
        this.tid = type
    }

    static traverseFiles(path, iter) {
        let files = fs.readdirSync(path)
        files.forEach(fileName => {
            let cpath = path + "/" + fileName
            let stat = fs.lstatSync(cpath)
            if (stat.isDirectory()) Parser.traverseFiles(cpath, iter)
            else iter(fileName, cpath)
        })
    }

    load(rawPath) {
        this.raws = []
        Parser.traverseFiles(rawPath, (fileName, path) => {
            let relative = path.substring(rawPath.length + 1)
            let json = this.readFile(fileName, path, relative)
            if (json) this.raws.push(json)
        })
    }

    parser() {
        this.results = []
        this.parserRuntime = {}
        this.raws.forEach(raw => {
            let json = this.toJson(raw)
            if (json) this.results.push(json)
        })
    }

    parserLang(json, key, id, obj) {
        if (!obj) return
        let p = `es.${this.tid}`
        let st = `${p}.${id}.${key}`
        json[key] = id
        this.langMap[st] = obj
    }

    firstUppercase(str) {
        return str.charAt(0).toUpperCase() + str.slice(1)
    }

    toCamel(str) {
        let keys = str.split("_")
        let list = [keys[0]]
        for (let i = 1; i < keys.length; i++)
            list.push(this.firstUppercase(keys[i]))
        return list.join("")
    }

    getOrCreateParserContext(key, _default) {
        if (this.parserRuntime[key] == undefined)
            this.parserRuntime[key] = _default
        return this.parserRuntime[key]
    }

    readFile(fileName, path) { }
    toJson(raw) { }
}

class TutorialParser extends Parser {

    constructor() {
        super("tutorial")
    }

    readFile(fileName, path) {
        let fparmas = fileName.split("_")
        let parmas = {}
        fparmas.forEach(e => {
            let dat = e.match(/(.+?)(\d+)/)
            if (dat) {
                let key = dat[1]
                let val = dat[2]
                parmas[key] = val
            }
        })
        let raw = JSON.parse(fs.readFileSync(path, "utf-8"))
        raw.level = parseInt(parmas["lv"])
        raw.unlock = parseInt(parmas["lk"])
        return raw
    }

    toJson(e) {
        assert(e.unlock != undefined, "unlock is miss")
        assert(e.level != undefined, "level is miss")

        let unlockMarkCountMap = this.getOrCreateParserContext("unlockMarkCountMap", {})
        let markKey = e.unlock + ":" + e.level
        let count = unlockMarkCountMap[markKey] | 0
        unlockMarkCountMap[markKey] = count + 1

        let json = {
            id: `${e.level}_${e.unlock}_${count}`,
            type: "elementalsorcery:tutorial",
            level: e.level,
            unlock: e.unlock,
            cover: e.cover,
            crafts: e.crafts,
            building: this.parserBuilding(e.building),
        }

        this.parserLang(json, "title", json.id, e.title)
        this.parserLang(json, "hover", json.id, e.hover)
        this.parserLang(json, "describe", json.id, e.describe)

        return json
    }

    parserBuilding(building) {
        if (!building) return undefined
        let config = {
            structure: building.structure,
            custom: []
        }
        for (let key in building.custom) {
            if (key.indexOf("->") != -1) {
                let result = key.match(/^(-?\d+),(-?\d+),(-?\d+)->(-?\d+),(-?\d+),(-?\d+)/)
                let x1 = parseInt(result[1])
                let y1 = parseInt(result[2])
                let z1 = parseInt(result[3])
                let x2 = parseInt(result[4])
                let y2 = parseInt(result[5])
                let z2 = parseInt(result[6])
                config.custom.push({
                    type: "full",
                    from: [x1, y1, z1],
                    to: [x2, y2, z2],
                    item: building.custom[key]
                })
                continue
            }
            let result = key.match(/^(-?\d+),(-?\d+),(-?\d+)/)
            let x = parseInt(result[1])
            let y = parseInt(result[2])
            let z = parseInt(result[3])
            config.custom.push({
                pos: [x, y, z],
                item: building.custom[key]
            })
        }
        for (let key in building.customBuildings) {
            let result = key.match(/^(-?\d+),(-?\d+),(-?\d+)/)
            let x = parseInt(result[1])
            let y = parseInt(result[2])
            let z = parseInt(result[3])
            config.custom.push({
                type: "building",
                pos: [x, y, z],
                structure: building.customBuildings[key]
            })
        }
        return config
    }
}

class ParchmentParser extends Parser {

    constructor() {
        super("page")
    }

    readFile(fileName, path, relative) {
        let start = relative.substring(0, relative.length - fileName.length - 1)
        let raw = JSON.parse(fs.readFileSync(path, "utf-8"))
        if (fileName.endsWith(".json")) fileName = fileName.substring(0, fileName.length - 5)
        if (start) raw.id = start + "_" + fileName
        else raw.id = fileName
        raw.camelName = this.toCamel(raw.id)
        return raw
    }

    toJson(raw) {

        let json = {
            id: raw.id,
            ver: 2,
            linked: raw.itemLink,
            icon: raw.cover,
        }

        this.pBelong(json, raw)

        let parserContext = { index: 0, raw: raw, base: raw }
        let hasAppends = raw.appends?.length > 0
        if (hasAppends) {
            json.type = "elementalsorcery:parchment_mult"
            json.lock = 0
            json.pages = []
            parserContext.json = {}
            this.pPage(parserContext)
            json.pages.push(parserContext.json)
            raw.appends.forEach(e => {
                parserContext.index = parserContext.index + 1
                parserContext.raw = e
                parserContext.json = {}
                this.pPage(parserContext)
                json.pages.push(parserContext.json)
            })
        } else {
            parserContext.json = json
            this.pPage(parserContext)
            json.type = `elementalsorcery:parchment_${json.type}`
        }

        return json
    }

    pBelong(json, raw) {
        if (raw.belong == "elf") json.level = -2
        else json.level = -1
    }

    pPage(context) {
        let base = context.base
        let json = context.json
        let raw = context.raw

        if (raw.craft) {
            json.type = "craft"
            json.item = raw.craft
        } else if (raw.research) {
            json.type = "research"
            json.item = raw.research
        } else {
            json.type = "normal"
            json.background = raw.background
            if (json.background == "inherit") json.item = raw.item || base.cover
        }

        {
            this.tLang(context, "title")
            let id = `${base.camelName}${context.index > 0 ? context.index : ""}`
            this.parserLang(json, "describe", id, raw["describe"])
        }
    }

    tLang(context, key, rKey) {
        let base = context.base
        let json = context.json
        let raw = context.raw
        let cKey = `val_${key}`
        rKey = rKey || key
        if (raw[key]) {
            let id = `${base.camelName}${context.index > 0 ? context.index : ""}`
            this.parserLang(json, rKey, id, raw[key])
            if (!context[cKey]) context[cKey] = id
        } else {
            if (!context[cKey]) throw "cannot find default value!"
            json[rKey] = context[cKey]
        }
    }
}

const cprocess = require("child_process")
const LANG_PATH = "../lang/langs/11_paperwork.json"
class Loader {

    constructor() {
        this.tlang = JSON.parse(fs.readFileSync(LANG_PATH, { encoding: "utf8", }))
    }

    finish() {
        fs.writeFileSync(LANG_PATH, JSON.stringify(this.tlang, null, '\t'), { encoding: "utf8", })
    }

    load(clazz, rawPath, assetsPath) {
        let parser = new clazz()
        parser.load(rawPath)
        parser.parser()
        this.tlang[`es.${parser.tid}`] = parser.langMap
        let srcPath = "../../src/main/resources/assets/elementalsorcery/" + assetsPath + "/"
        // fs.mkdirSync(srcPath, { recursive: true })
        parser.results.forEach(e => {
            let path = srcPath + e.id + ".json"
            delete e.id
            fs.writeFileSync(path, JSON.stringify(e), { encoding: "utf8", })
        })
    }

    exec(cd, cmd) {
        cprocess.exec(cmd, { cwd: cd }, function (err, stdout) {
            if (err) console.log(err)
            else console.log(stdout)
        })
    }
}

let loader = new Loader()
// loader.load(TutorialParser, "./tutorials", "tutorials")
loader.load(ParchmentParser, "./parchments", "parchments")
loader.finish()

loader.exec('../lang', `node ./json_to_lang.js`)
// loader.exec('../lang', `node ./parchments_dispose.js`)