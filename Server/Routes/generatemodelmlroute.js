const router = require('express').Router();

const { spawn } = require('child_process');
const { cwd } = require('process');

router.post('/', function (req, res) {
	var data = req.body
	console.log(data)
	const python = spawn('python', ['Python Scripts/createmodelml.py'])
	python.stdin.write(req.session.preprocessed_data_path + '\n')
	python.stdin.write(JSON.stringify(data))
	python.stdin.end()

	var parsedData = "";
	python.stdout.on('data', function (data) {
		parsedData = data.toString()
		console.log("Data printed", parsedData)
		//req.io.emit('logs', parsedData)
	})

	python.stderr.on('data', function (data) {
		console.log(data.toString())
	});

	python.stdout.on('end', function () {
		var file_path = parsedData.replace("\r\n", "");
		console.log("Updated File path:",file_path)
		const python1 = spawn('python', [file_path])
		python1.stdout.on('data', (data) =>{
			data = data.toString()
			console.log(data)
			req.io.emit('logs', data)
		})

		python1.stdout.on('end', () =>{
			data = data.toString()
			res.send({ text: "OK" })
		})

		python1.stderr.on('data', (data) =>{
			data = data.toString()
			console.log("Error:", data)
			req.io.emit('logs', data)
		})
	})

	python.on('exit', (code) => {
		console.log("Process quit with code : " + code);
	});
})

module.exports = router