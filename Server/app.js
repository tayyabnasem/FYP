const express = require('express')
const cors = require('cors')
const bodyParser = require('body-parser')
const session = require('express-session');

const app = express()
const signinroute = require('./Routes/signin')
const signuproute = require('./Routes/signup')
const signinWithGoogleroute = require('./Routes/signinwithgoogle')
const uploadFileroute = require('./Routes/fileupload')
const getprojectsroute = require('./Routes/getprojects')
const filterdataroute = require('./Routes/filterdata')
const plotroute = require('./Routes/plottingroute')
const signoutroute = require('./Routes/signoutroute')

var jsonParser = bodyParser.json()

app.use(cors({ origin: "http://localhost:4200", credentials: true }))
app.use(session({ resave: true, secret: '123456', saveUninitialized: true }));
app.use(jsonParser)

app.use('/signin', signinroute)
app.use('/signup', signuproute)
app.use('/signinWithGoogle', signinWithGoogleroute)
app.use('/uploadFile', uploadFileroute)
app.use('/getProjects', getprojectsroute)
app.use('/filterData', filterdataroute)
app.use('/plotData', plotroute)
app.use('/signout', signoutroute)

app.use(express.static(__dirname));

app.listen(3000, function () {
	console.log("server running")
})