const http = require('http');

const data = JSON.stringify({
  username: "testuser2",
  gmail: "testuser2@gmail.com",
  password: "password123",
  role: "STAFF"
});

const options = {
  hostname: 'localhost',
  port: 8080,
  path: '/api/accounts',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length,
    'Authorization': 'Bearer fake_token_here'
  }
};

const req = http.request(options, (res) => {
  console.log(`STATUS: ${res.statusCode}`);
  res.on('data', (d) => process.stdout.write(d));
});

req.on('error', (error) => {
  console.error(error);
});

req.write(data);
req.end();
