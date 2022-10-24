const cors = require("cors");
const express = require("express");
const fs = require("fs");

const app = express();
const PORT = 3000;

app.use(express.json());
app.use (cors({
    origin: function (origin, callback){
        return callback(null, true);
    }
}));





/*Obrir Servidor*/
app.listen(PORT, () => {
    console.log("Server RUNNING ["+PORT+"]");
});