import axios from 'axios';

const publicApi  = axios.create({
    baseURL: 'http://localhost:8888'
});

export default publicApi;