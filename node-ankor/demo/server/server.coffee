http = require("http")
express = require("express")
EcoTemplates = require("./helper/EcoTemplates")
{AnkorMan} = require("./AnkorMan")

ankorMan = new AnkorMan()
ankor = ankorMan.contextManager.registry["ankorService"].ankor

app = express()
app.engine("eco", EcoTemplates)
app.set("port", 8080)
app.set("views", "#{__dirname}/templates")
app.set("view engine", "eco")

app.use("/js/dojo", express.static("#{__dirname}/../lib/dojo"))
app.use("/js/dijit", express.static("#{__dirname}/../lib/dijit"))
app.use("/js/dojox", express.static("#{__dirname}/../lib/dojox"))
app.use(express.static("#{__dirname}/public"))

app.use(express.bodyParser())
app.use(express.multipart())
app.use(express.cookieParser())

app.use(express.session({
    secret: "0c4b4b78-df35-11e2-ab63-577e3325d7fc"
}))

app.use(ankor.transport.middleware())
app.use(app.router)

server = http.createServer(app)
server.listen(app.get("port"), ->
    ankorMan.init(app)
    console.log("Ankor demo is listening on port #{app.get("port")}")
)
