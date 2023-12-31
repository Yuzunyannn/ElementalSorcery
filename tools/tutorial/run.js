const { assert } = require('console')
var fs = require('fs')

let tutorialList = []
let tutorialFiles = fs.readdirSync("./tutorials")
tutorialFiles.forEach(fileName => {
    let path = "./tutorials/" + fileName
    let fparmas = fileName.split("_")
    let parmas = {}
    fparmas.forEach(e => {
        let dat = e.match(/(.+)(\d+)/)
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
let unlockCountMap = {}

function doLang(json, obj, key) {
    if (!obj) return
    let st = "es.tutorial." + key + "." + json.id
    json[key] = json.id
    tutorialLang[st] = obj
}

tutorialList.forEach(e => {

    assert(e.unlock != undefined, "unlock is miss")
    assert(e.level != undefined, "level is miss")

    let count = unlockCountMap[e.unlock] | 0
    unlockCountMap[e.unlock] = count + 1

    let json = {
        id: `${e.level}_${e.unlock}_${count}`,
        type: "elementalsorcery:tutorial",
        level: e.level,
        unlock: e.unlock,
        cover: e.cover,
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
    fs.writeFileSync(path, JSON.stringify(e, null, '\t'), { encoding: "utf8", })
})

const cprocess = require("child_process")
cprocess.exec(`node ./json_to_lang.js`, { cwd: '../lang' }, function (err, stdout) {
    if (err) console.log(err)
    else console.log(stdout)
})