import axios from 'axios';
import publicApi from './publicApi';

const privateApi = axios.create({
    baseURL: 'http://localhost:8888'
});

// 헤더에 엑세스 토큰을 추가한 뒤 api 요청
privateApi.interceptors.request.use(config => {
    const accessToken = localStorage.getItem('Authorization');
    
    if (accessToken) {
        config.headers['Authorization'] = accessToken;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

// api 요청 시 마다 엑세스 토큰이 만료되었을 경우 리프래시 토큰으로 재발급 요청
privateApi.interceptors.response.use(response => {

    return response;

    }, async error => {

    const originalRequest = error.config;

    if (error.response.status === 401 && !originalRequest._retry) {

        originalRequest._retry = true;

        try {
        // 엑세스 토큰 재발급
        localStorage.removeItem('Authorization');
        const response = await publicApi.post('/auth/reissue', {}, { withCredentials: true });
        const accessToken = response.headers['authorization'];

        localStorage.setItem('Authorization', accessToken);

        // 재발급 받은 엑세스 토큰으로 원래 요청 재시도
        originalRequest.headers['Authorization'] = accessToken;
        
        return publicApi(originalRequest);

        } catch (refreshError) {

        // 리프레시 토큰 만료 또는 에러 처리
        console.error('리프래시 토큰 만료 또는 에러 ', refreshError);
        localStorage.removeItem('Authorization');

        window.location.href = '/login'; // 로그인 페이지로 리다이렉션

        return Promise.reject(refreshError);
        }
    }
    return Promise.reject(error);
});

export default privateApi;