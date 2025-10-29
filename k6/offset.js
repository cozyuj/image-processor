import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,
    iterations: 1000,
    thresholds: {
        http_req_duration: ['p(95)<120'], 
        checks: ['rate>0.99'],           
    },
};

export default function () {
    const res = http.get('http://localhost:8080/api/v1/projects?id=1&page=1&size=50');
    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(0.1);
}