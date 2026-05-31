import http from 'k6/http';
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
            executor: 'shared-iterations',
            vus: 20,
            iterations: 10000,
            startTime: '35s',
            exec: 'measure',
        },
    },
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

const searchDuration = new Trend('search_duration', true);

const SEARCH_URL = __ENV.SEARCH_URL || 'http://localhost:8080/load-test/members/prefix/es';
const KEYWORD = '김';

function sendSearch() {
    const url = `${SEARCH_URL}?keyword=${encodeURIComponent(KEYWORD)}`;

    const res = http.get(url);

    if (res.status !== 200) {
        console.log(`keyword=${KEYWORD}, status=${res.status}, body=${res.body}`);
    }

    return res;
}

export function warmup() {
    sendSearch();
}

export function measure() {
    const res = sendSearch();
    searchDuration.add(res.timings.duration);
}
