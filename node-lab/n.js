

var mysql = require("mysql");
var conn = mysql.createConnection({host: "localhost", user: "root", password: "", database: "ca"});
conn.query("select * from carrera", function (error, data){
    console.log(data); 
});

