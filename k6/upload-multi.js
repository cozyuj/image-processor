import http from 'k6/http';
import { check } from 'k6';

const imgDir = 'img/';
const fileNames = ['sample1.png', 'sample2.png', 'sample3.png'];

const fileDatas = fileNames.map(name => {
    const file = open(`${imgDir}${name}`, 'b');
    return http.file(file, name, 'image/png');
});

export const options = {
    vus: 50,
    iterations: 50,
};

export default function () {
    const formData = {
        files: fileDatas,
        memo: 'multi-upload test',
        tags: 'tag1,tag2,tag3',
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