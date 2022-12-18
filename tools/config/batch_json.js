const readline = require('readline')
const fs = require("fs")
const path = require("path")
const base = "../../src/main/resources/assets/elementalsorcery/"
let root = process.argv[2] || "element_recipes"
root = path.join(base, root)

function travel(dir, callback) {
    fs.readdirSync(dir).forEach(file => {
        let pathname = path.join(dir, file)
        if (fs.statSync(pathname).isDirectory()) travel(pathname, callback)
        else callback(pathname)
    })
}


// console.log("路径: " + root)

travel(root, (root) => {
    if (path.extname(root) != ".json") return
    // console.log("处理json: " + root)
    let json = JSON.parse(fs.readFileSync(root))
    json = Object.assign({ type: "elementalsorcery:element_map" }, json)
    // json.type = "elementalsorcery:element_map"
    if (json.type == "inherit_element") {
        console.log("修改：" + root)
    }
    fs.writeFileSync(root, JSON.stringify(json, undefined, '\t'))
})