import http from 'k6/http';

const UPLOAD_URL = __ENV.UPLOAD_URL || 'cloudfront or s3 url';

export const options = {
    vus: 5,
    iterations: 100,
    thresholds: {
        http_req_duration: ['p(99)<1000'],
    },
};

export default function () {
    http.get(UPLOAD_URL);
}