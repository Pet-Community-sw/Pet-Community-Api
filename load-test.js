import http from 'k6/http';
import {check, sleep} from 'k6';

export const options = {
    stages: [
        {duration: '30s', target: 100},
        {duration: '30s', target: 200},
        {duration: '30s', target: 400},
        {duration: '30s', target: 800},
        {duration: '30s', target: 0},
    ],
};


export function setup() {
    const loginRes = http.post(
        'http://localhost:8080/members/login',
        JSON.stringify({
            email: 'chltjswo@naver.com',
            password: 'sunjaeJang12!',
        }),
        {
            headers: {'Content-Type': 'application/json'},
        }
    );

    const token = loginRes.json('accessToken');

    return token;  // VU들에게 전달됨
}

export default function (token) {
    const keyword = "ㅊㅅ"; // 한글도 OK
    const res = http.get(
        `http://localhost:8080/members/auto-complete?keyword=${encodeURIComponent(keyword)}`,
        {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    console.log(res.status);

    check(res, {
        'status 200': (r) => r.status === 200,
    });

    sleep(1);
}
