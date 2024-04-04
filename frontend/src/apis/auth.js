import privateApi from './privateApi';
import publicApi from './publicApi';

// 로그인
export const login = (username, password) => privateApi.post(`/login?username=${username}&password=${password}`)

// 로그아웃
export const logout = () => publicApi.post(`/logout`, {}, { withCredentials: true })

// 사용자 정보
export const info = () => privateApi.get(`/users/info`)

// 친구 리스트
export const friendsList = () => privateApi.get(`/users`)

// 회원 가입 
export const join = (data) => privateApi.post(`/users`, data)

// 회원 정보 수정
export const update = (data) => privateApi.put(`/users`, data)

// 회원 탈퇴
export const remove = (userId) => privateApi.delete(`/users/${userId}`)