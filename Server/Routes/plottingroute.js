const router = require('express').Router();
const { spawn } = require('child_process');

router.post('/', function (req, res) {
    let sess = req.session
	dataToPass = req.body
	const python = spawn('python', ['Python Scripts/script.py'])

	python.stdin.write(sess.preprocessed_data_path + '\n')
	python.stdin.write(JSON.stringify(dataToPass))
	python.stdin.end()

	var parsedData = "";
	python.stdout.on('data', function (data) {
		//console.log("Data printed", data.toString())
		parsedData += data.toString()
	})

	python.stderr.on('data', function (data) {
		console.log(data.toString())
	});

	python.stdout.on('end', function () {
		console.log(parsedData, "END")
		res.send({ imagePath: parsedData })
	})

	python.on('exit', (code) => {
		console.log("Process quit with code : " + code);
	});
})

module.exports = router