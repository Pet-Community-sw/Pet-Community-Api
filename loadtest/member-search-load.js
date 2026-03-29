import http from "k6/http";
import {check, sleep} from "k6";
import {Counter, Rate, Trend} from "k6/metrics";

const latency = new Trend("ac_latency_ms", true);
const okRate = new Rate("ac_ok_rate");
const failCount = new Counter("ac_fail_count");

export const options = {
    scenarios: {
        warmup: {
            executor: "constant-vus",
            vus: 5,
            duration: "20s",
            exec: "warmup",
            gracefulStop: "0s",
        },

        load: {
            executor: "ramping-vus",
            startVUs: 0,
            stages: [
                {duration: "20s", target: 50},
                {duration: "40s", target: 200},
                {duration: "60s", target: 200},
                {duration: "20s", target: 0},
            ],
            exec: "test",
            gracefulRampDown: "10s",
        },
    },
};

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const ENDPOINT = __ENV.ENDPOINT || "/members/auto-complete";
const TOKEN =
    __ENV.TOKEN ||
    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIzIiwibmFtZSI6IuyViOuFle2VmOyEuOyalCIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE3NzI4NTg2OTMsImV4cCI6MTc3Mjk0NTA5M30.AVzPTdtcZgmV6rjzbHMJWqA0U2i3bLi-4MwolpWNPuM";

const Q_PARAM = __ENV.Q_PARAM || "keyword";
const LIMIT_PARAM = __ENV.LIMIT_PARAM || "size";
const LIMIT_VALUE = __ENV.LIMIT_VALUE || "10";

const HOT_KEYS = ["김", "이", "박", "최", "정", "김민", "이서", "박지", "최서", "정하"];

const WARM_KEYS = [
    "김",
    "김민",
    "김서",
    "김지",
    "김하",
    "이",
    "이서",
    "이지",
    "이하",
    "이윤",
    "박",
    "박지",
    "박서",
    "박민",
    "박하",
    "최",
    "최지",
    "최서",
    "최민",
    "정",
    "정서",
    "정민",
    "정지",
];

// 랜덤 한글 검색어를 생성하는 함수
function randomHangul(len) {
    const start = 0xac00;
    const end = 0xd7a3;
    let s = "";
    for (let i = 0; i < len; i++) {
        const code = start + Math.floor(Math.random() * (end - start));
        s += String.fromCharCode(code);
    }
    return s;
}

//검색어 섞음
function pickKeyword() {
    const r = Math.random();
    if (r < 0.6) return HOT_KEYS[Math.floor(Math.random() * HOT_KEYS.length)];
    if (r < 0.9) return WARM_KEYS[Math.floor(Math.random() * WARM_KEYS.length)];
    return randomHangul(Math.random() < 0.5 ? 1 : 2);
}

// 요청에 사용할 최종 URL을 생성하는 함수
function buildUrl(keyword) {
    const q = encodeURIComponent(keyword);
    let url = `${BASE_URL}${ENDPOINT}?${Q_PARAM}=${q}`;

    if (LIMIT_PARAM && LIMIT_VALUE) {
        url += `&${LIMIT_PARAM}=${encodeURIComponent(LIMIT_VALUE)}`;
    }

    return url;
}

// 공통 헤더 생성
function buildHeaders() {
    const headers = {Accept: "application/json"};
    if (TOKEN) headers["Authorization"] = TOKEN;
    return headers;
}

//본 테스트 앞서 웜업
export function warmup() {
    const headers = buildHeaders();

    const keyword =
        Math.random() < 0.7
            ? HOT_KEYS[Math.floor(Math.random() * HOT_KEYS.length)]
            : WARM_KEYS[Math.floor(Math.random() * WARM_KEYS.length)];

    const url = buildUrl(keyword);
    const res = http.get(url, {headers});

    latency.add(res.timings.duration);
    const ok = check(res, {"200": (r) => r.status === 200});
    okRate.add(ok);
    if (!ok) failCount.add(1);

    sleep(0.1);
}

export function test() {
    const headers = buildHeaders();

    const keyword = pickKeyword();
    const url = buildUrl(keyword);

    const res = http.get(url, {headers});

    latency.add(res.timings.duration);

    const ok = check(res, {
        "status is 200": (r) => r.status === 200,
        "content-type json": (r) =>
            (r.headers["Content-Type"] || "").includes("application/json"),
    });

    okRate.add(ok);
    if (!ok) failCount.add(1);

    sleep(0.05);
}