const express = require('express')
const cors = require('cors')
const bodyParser = require('body-parser')
const session = require('express-session');

const app = express()
const signinroute = require('./Routes/signin')
const signuproute = require('./Routes/signup')
const signinWithGoogleroute = require('./Routes/signinwithgoogle')
const uploadFileroute = require('./Routes/fileupload')
const createprojectroute = require('./Routes/createprojectroute')
const getprojectsroute = require('./Routes/getprojects')
const filterdataroute = require('./Routes/filterdata')
const plotroute = require('./Routes/plottingroute')
const signoutroute = require('./Routes/signoutroute')
const generatemodelroute = require('./Routes/generatemodelroute')
const getDataStisticsroute = require('./Routes/getDatasetStatistics')
const getColumnsForPlotroute = require('./Routes/getcolumnsforplotroute')
const saveModelroute = require('./Routes/savemodel')
const getModelInforoute = require('./Routes/getmodelinforoute')
const saveHyperparameterroute = require('./Routes/savehyperparametersroute')

var jsonParser = bodyParser.json()

app.use(cors({ origin: "http://localhost:4200", credentials: true }))
app.use(session({ resave: true, secret: '123456', saveUninitialized: true }));
app.use(jsonParser)

app.use('/signin', signinroute)
app.use('/signup', signuproute)
app.use('/signinWithGoogle', signinWithGoogleroute)
app.use('/uploadFile', uploadFileroute)
app.use('/createProject', createprojectroute)
app.use('/getProjects', getprojectsroute)
app.use('/filterData', filterdataroute)
app.use('/plotData', plotroute)
app.use('/signout', signoutroute)
app.use('/generatemodel', generatemodelroute)
app.use('/getDatasetStaictics', getDataStisticsroute)
app.use('/getColumns', getColumnsForPlotroute)
app.use('/saveModel', saveModelroute)
app.use('/getModelInfo', getModelInforoute)
app.use('/saveHyperparameter', saveHyperparameterroute)

app.use(express.static(__dirname));

app.listen(3000, function () {
	console.log("server running")
})