const router = require('express').Router();

const { spawn } = require('child_process');

router.post('/', function (req, res) {
	var data = req.body

	//console.log(data)
	//const python = spawn('python', ['temp.py'])
	const python = spawn('python', ['Python Scripts/createmodel.py'])
	python.stdin.write(req.session.preprocessed_data_path + '\n')
	python.stdin.write(JSON.stringify(data))
	python.stdin.end()

	var parsedData = "";
	python.stdout.on('data', function (data) {
		data = data.toString()
		console.log("Data printed", data)
		//req.io.emit('logs', data)
	})

	python.stderr.on('data', function (data) {
		console.log(data.toString())
	});

	python.stdout.on('end', function () {
		console.log(parsedData)
		res.send({ text: "OK" })
	})

	python.on('exit', (code) => {
		console.log("Process quit with code : " + code);
	});
})

module.exports = router