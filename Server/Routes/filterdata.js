const router = require('express').Router();
const { spawn } = require('child_process');

router.post('/', function (req, res) {
	let sess = req.session
	data = req.body
	values_to_fill = []
	console.log(sess.filePath)

	const python = spawn('python', ['Python Scripts/filterData.py'])

	python.stdin.write(sess.filePath + '\n')
	python.stdin.write(JSON.stringify(data))
	python.stdin.end()

	var parsedData = "";
	python.stdout.on('data', function (data) {
		console.log("Data printed", data.toString())
		parsedData += data.toString()
	})

	python.stderr.on('data', function (data) {
		console.log(data.toString())
	});

	python.stdout.on('end', function () {
		parsedData.replace('/r/n', '')
		console.log(parsedData)
		sess.preprocessed_data_path = sess.filePath.split(".csv")[0]+"_preprocessed.csv"
		console.log('Preprocess: ',sess.preprocessed_data_path)
		res.send({ text: "Success", err: "None" })
	})

	python.on('exit', (code) => {
		console.log("Process quit with code : " + code);
	});
})

module.exports = router