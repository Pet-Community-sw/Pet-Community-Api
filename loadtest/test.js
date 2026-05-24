import http from 'k6/http';
import {check} from 'k6';

export const options = {
    stages: [
        {duration: '10s', target: 30},
        {duration: '20s', target: 80},
        {duration: '30s', target: 150},
        {duration: '30s', target: 150},
        {duration: '10s', target: 0},
    ],
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

const BASE_URL = 'http://localhost:8080';
const keywords = ['산', '강', '멍', '댕', '행', '친'];
export default function () {
    const keyword = keywords[Math.floor(Math.random() * keywords.length)];

    const res = http.get(
        `${BASE_URL}/members/test?keyword=${encodeURIComponent(keyword)}`
    );

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

}