import http from 'k6/http';
import exec from 'k6/execution';
import {Trend} from 'k6/metrics';

export const options = {
    scenarios: {
        warmup: {
            executor: 'constant-vus', //vu 고정으로 duration동안 계속 요청
            vus: 5,
            duration: '30s',
            exec: 'warmup',
        },
        measure: {
            executor: 'shared-iterations', //전체 iterations를 vu에 나눠서 실행
            vus: 20,
            iterations: 10000,
            startTime: '35s',
            exec: 'measure', //실행할 함수
        },
    },
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

const signupDuration = new Trend('signup_duration', true); //웜업 요청 빼고 실제 측정할 때만 설정
const SIGNUP_URL = `http://localhost:8080/load-test/members/sync-index`;

function uniqueSuffix() {
    return `${exec.scenario.name}_${__VU}_${__ITER}`;
}

function sendSignup() {
    const suffix = uniqueSuffix();
    const email = `user${suffix}@test.com`;

    const payload = JSON.stringify({
        name: '테스트유저',
        email: email,
        password: 'Test1234!',
        phoneNumber: '01012345678',
    });

    const res = http.post(SIGNUP_URL, payload, {
        headers: {
            'Content-Type': 'application/json',
        },
    });
    if (res.status !== 201) {
        console.log(
            `email=${email}, status=${res.status}, body=${res.body}`
        );
    }

    return res;
}

export function warmup() {
    sendSignup();
}

export function measure() {
    const res = sendSignup();
    signupDuration.add(res.timings.duration);
}
