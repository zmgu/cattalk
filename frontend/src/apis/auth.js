import privateApi from './privateApi';

// 로그인
export const login = (username, password) => privateApi.post(`/login?username=${username}&password=${password}`)

export const createAccessToken = () => {
    return privateApi.get('/auth/reissue', {
        withCredentials: true 
    });
}

// 사용자 정보
export const info = () => privateApi.get(`/users/info`)

// 회원 가입 
export const join = (data) => privateApi.post(`/users`, data)

// 회원 정보 수정
export const update = (data) => privateApi.put(`/users`, data)

// 회원 탈퇴
export const remove = (userId) => privateApi.delete(`/users/${userId}`)