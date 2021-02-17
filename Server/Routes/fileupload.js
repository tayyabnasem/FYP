const multer = require('multer')
const fs = require('fs')
const { spawn } = require('child_process')
const router = require('express').Router();

const DIR = './Uploads/uploads';

const storage = multer.diskStorage({
	destination: (req, file, cb) => {
		let dir = DIR + req.session.email
		if (!fs.existsSync(dir)) {
			fs.mkdirSync(dir);
		}
		cb(null, dir);
	},
	filename: (req, file, cb) => {
		cb(null, file.originalname);
	}
});

router.post('/', function (req, res) {
	sess = req.session;
	var uploadPost = multer({ storage: storage }).single('file');
	uploadPost(req, res, function (err) {
		if (err) {
			return res.end("error uploading file");
		}

		let filePath = req.file.path
		sess.filePath = filePath
		toSend = []

		const python = spawn('python', ['Python Scripts/getStats.py'])

		python.stdin.write(filePath)
		python.stdin.end()

		var parsedData = "";
		python.stdout.on('data', function (data) {
			// console.log("Data printed", data.toString())
			parsedData += data.toString()
		})

		python.stderr.on('data', function (data) {
			console.log(data.toString())
		});

		python.stdout.on('end', function () {
			parsedData.replace('/r/n', '')
			//console.log(JSON.parse(parsedData), "END")
			res.send(JSON.parse(parsedData))
		})

		python.on('exit', (code) => {
			console.log("Process quit with code : " + code);
		});
	});
});

module.exports = router;