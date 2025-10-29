import http from 'k6/http';
import { check } from 'k6';

const filePath = 'img/sample1.png';
const fileName = filePath.split('/').pop();
const fileData = open(filePath, 'b');

export const options = {
    vus: 50,
    iterations: 50,
};

export default function () {
    const formData = {
        files: http.file(fileData, fileName, 'image/png'),
        memo: 'test',
        tags: 'tag1,tag2'
    };

    const res = http.post('http://localhost:8080/api/v1/projects/1/images', formData);

    const success = check(res, {
        'status is 200': (r) => r.status === 200,
    });

    if (!success) {
        console.log(`Status: ${res.status}`);
        console.log(`Response: ${res.body}`);
    } else {
        console.log(`Uploaded successfully with status: ${res.status}`);
    }
}