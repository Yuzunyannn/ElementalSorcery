const { assert } = require('console')
var fs = require('fs')

function traverseFiles(path, iter) {
    let files = fs.readdirSync(path)
    files.forEach(fileName => {
        let cpath = path + "/" + fileName
        let stat = fs.lstatSync(cpath)
        if (stat.isDirectory()) traverseFiles(cpath, iter)
        else iter(fileName, cpath)
    })
}

let tutorialList = []
traverseFiles("./tutorials", (fileName, path) => {
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
    let tutorial = JSON.parse(fs.readFileSync(path, "utf-8"))
    tutorial.level = parseInt(parmas["lv"])
    tutorial.unlock = parseInt(parmas["lk"])
    tutorialList.push(tutorial)
})

let tutorialLang = {}
let fileList = []
let unlockMarkCountMap = {}

function doLang(json, obj, key) {
    if (!obj) return
    let st = "es.tutorial." + key + "." + json.id
    json[key] = json.id
    tutorialLang[st] = obj
}

function parserBuilding(building) {
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

tutorialList.forEach(e => {

    assert(e.unlock != undefined, "unlock is miss")
    assert(e.level != undefined, "level is miss")

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
        building: parserBuilding(e.building),
    }

    doLang(json, e.title, "title")
    doLang(json, e.hover, "hover")
    doLang(json, e.describe, "describe")

    fileList.push(json)
})


let srcPath = "../../src/main/resources/assets/elementalsorcery/tutorials/"
let langPath = "../lang/langs/11_tutorial.json"

tlang = JSON.parse(fs.readFileSync(langPath, { encoding: "utf8", }))
tlang["es.tutorial"] = tutorialLang
fs.writeFileSync(langPath, JSON.stringify(tlang, null, '\t'), { encoding: "utf8", })
fileList.forEach(e => {
    let path = srcPath + e.id + ".json"
    delete e.id
    fs.writeFileSync(path, JSON.stringify(e), { encoding: "utf8", })
})

const cprocess = require("child_process")
cprocess.exec(`node ./json_to_lang.js`, { cwd: '../lang' }, function (err, stdout) {
    if (err) console.log(err)
    else console.log(stdout)
})
cprocess.exec(`node ./parchments_dispose.js`, { cwd: '../lang' }, function (err, stdout) {
    if (err) console.log(err)
    else console.log(stdout)
})