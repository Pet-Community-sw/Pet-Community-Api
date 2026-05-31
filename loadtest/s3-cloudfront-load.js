import http from 'k6/http';

export const options = {
    vus: 5,
    iterations: 100,
    summaryTrendStats: ['avg', 'min', 'med', 'max', 'p(90)', 'p(95)', 'p(99)'],
};

const UPLOAD_URL = __ENV.UPLOAD_URL || 'cloudfront or s3 url';

export default function () {
    http.get(UPLOAD_URL);
}