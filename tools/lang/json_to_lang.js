var fs = require('fs')

let jsonLang = {}
let langfiles = fs.readdirSync("./langs")
langfiles.forEach(fileName => {
    let path = "./langs/" + fileName
    let jd = JSON.parse(fs.readFileSync(path, "utf-8"))
    for (key in jd) jsonLang[key] = jd[key]
})

let outputTalbe = {
    zh_CN: "",
    en_US: "",
    ja_JP: ""
}

for (let lang in outputTalbe) {
    let str = outputTalbe[lang]
    str = str + "#PARSE_ESCAPES\r\n"
    outputTalbe[lang] = str
}

for (let lang in outputTalbe) {
    let str = outputTalbe[lang]
    for (let type in jsonLang) {
        str = str + "\r\n"
        let data = jsonLang[type]
        for (let key in data) {
            let valueMap = data[key]
            let value = valueMap[lang] ? valueMap[lang] : valueMap["zh_CN"]
            str = str + key + "=" + value + "\r\n"
        }
    }
    outputTalbe[lang] = str
}

for (let lang in outputTalbe) {
    let str = outputTalbe[lang]
    let path = "../../src/main/resources/assets/elementalsorcery/lang/" + lang + ".lang"
    console.log("update " + path)
    fs.writeFileSync(path, str, { encoding: "utf8", });
}

