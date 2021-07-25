var fs = require('fs')

function loadOriginLang(name) {
    let fileData = fs.readFileSync("../../src/main/resources/assets/elementalsorcery/lang/" + name + ".lang", "utf-8")
    let lines = fileData.split("\r\n")
    let dataMap = {}
    lines.forEach(line => {
        let i = line.indexOf("=")
        if (i == -1) return;
        let key = line.substring(0, i)
        let value = line.substring(i + 1)
        dataMap[key] = value
    })
    return dataMap
}

let zh_CN = loadOriginLang("zh_CN")
let en_US = loadOriginLang("en_US")
let ja_JP = loadOriginLang("ja_JP")

let outputMap = {}
for (let key in zh_CN) {
    let ks = key.split(".")
    let first = ks[0]
    let second = ks[1]

    let filter = first
    if (first == "es") {
        filter = "es." + second
    } else if (first == "info") {
        if (second == "element") filter = "info.element"
    }
    // filter = typeMap[filter] ? typeMap[filter] : filter

    if (!outputMap[filter]) outputMap[filter] = {}
    outputMap[filter][key] = {
        "zh_CN": zh_CN[key],
        "en_US": en_US[key],
        "ja_JP": ja_JP[key],
    }
}

fs.writeFileSync("./lang.json", JSON.stringify(outputMap, null, 2), { encoding: "utf8", });