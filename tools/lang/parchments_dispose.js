const fs = require('fs')
const path = require('path')

function walkSync(root, callback) {
    fs.readdirSync(root, { withFileTypes: true }).forEach((dirent) => {
        let filePath = path.join(root, dirent.name);
        if (dirent.isFile()) callback(filePath, dirent)
        else if (dirent.isDirectory()) walkSync(filePath, callback)
    });
}

let pageJson = JSON.parse(fs.readFileSync("./langs/10_page.json"))
let pageMap = pageJson.page

function drop(key) {
    let old = pageMap[key]
    if (!old) return
    pageMap[key] = { dispose: true }
    for (let k in old) pageMap[key][k] = old[k]
}

function travel(pJson) {
    let type = pJson.type
    if (type.indexOf(":") != -1) {
        let result = type.match(/(.*):(.*)/)
        type = result[2]
    }
    if (type == "parchment_mult") {
        pJson.pages.forEach(e => travel(e))
    } else {
        let name = pJson.name
        drop(`page.${name}`)
        let differ = pJson.differ
        if (differ) drop(`page.${name}.ct.${differ}`)
        else drop(`page.${name}.ct`)
    }
}

let totalCount = 0
let disposeCount = 0
walkSync("../../src/main/resources/assets/elementalsorcery/parchments/", file => {
    let parchmentJson = JSON.parse(fs.readFileSync(file))
    if (parchmentJson.ver == 2) return
    totalCount++
    if (!parchmentJson.dispose) {
        console.log(file)
        return
    }
    disposeCount++
    travel(parchmentJson)
})

fs.writeFileSync("./langs/10_page.json", JSON.stringify(pageJson, null, 4), { encoding: "utf8", });

console.log(`数据统计：主进度：${(disposeCount / totalCount * 100).toFixed(2)}%(${disposeCount}/${totalCount})`)
console.log(`开始未迁移文案：`)
for (let key in pageMap) {
    let lang = pageMap[key]
    if (!lang.dispose) console.log(key)
}