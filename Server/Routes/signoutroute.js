const router = require('express').Router();

router.get('/', (req, res) => {
    req.session = null
    console.log(req.session)
    res.send({ text: "Succesfully Logged out" })
})

module.exports = router