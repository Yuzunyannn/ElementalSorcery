var fs = require('fs')
let jsonData = fs.readFileSync("./lang.json", "utf-8")
let jsonLang = JSON.parse(jsonData)

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
    fs.writeFileSync("../../src/main/resources/assets/elementalsorcery/lang/" + lang + ".lang", str, { encoding: "utf8", });
}