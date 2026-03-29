import http from 'k6/http';
import {check} from 'k6';
import {Counter, Trend} from 'k6/metrics';

export const options = {
    scenarios: {
        signup_stage_1: {
            executor: 'shared-iterations',
            vus: 5,
            iterations: 1000,
            maxDuration: '30s',
            startTime: '0s',
        },
        signup_stage_2: {
            executor: 'shared-iterations',
            vus: 10,
            iterations: 2000,
            maxDuration: '1m',
            startTime: '30s',
        },
        signup_stage_3: {
            executor: 'shared-iterations',
            vus: 15,
            iterations: 3000,
            maxDuration: '1m30s',
            startTime: '1m30s',
        },
        signup_stage_4: {
            executor: 'shared-iterations',
            vus: 20,
            iterations: 4000,
            maxDuration: '2m',
            startTime: '3m',
        },
    },
};

const SIGNUP_URL = `http://localhost:8080/members`;

const signupDuration = new Trend('signup_api_duration');
const successCount = new Counter('signup_success_count');
const failCount = new Counter('signup_fail_count');

const validNames = [
    '민수',
    '서연',
    '지훈',
    '수빈',
    '선재',
    '지우',
    '하린',
    '도윤',
    '지민',
    '유진',
];

function uniqueSuffix() {
    return `${Date.now()}_${__VU}_${__ITER}`;
}

function makePhoneNumber() {
    const n = 10000000 + ((__VU * 100000 + __ITER) % 90000000);
    return `010${String(n).padStart(8, '0')}`;
}

export default function () {
    const suffix = uniqueSuffix();
    const name = validNames[(__VU + __ITER) % validNames.length];
    const phoneNumber = makePhoneNumber();
    const email = `user${suffix}@test.com`;

    const payload = JSON.stringify({
        name: name,
        email: email,
        password: 'Test1234!',
        phoneNumber: phoneNumber,
    });

    const res = http.post(SIGNUP_URL, payload, {
        headers: {
            'Content-Type': 'application/json; charset=utf-8',
        },
        timeout: '10s',
    });

    signupDuration.add(res.timings.duration);

    const ok = check(res, {
        'status is 201': (r) => r.status === 201,
    });

    if (ok) {
        successCount.add(1);
    } else {
        failCount.add(1);
        console.log(
            `name=${name}, email=${email}, phone=${phoneNumber}, status=${res.status}, body=${res.body}`
        );
    }
}