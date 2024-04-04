/*
 *  로그인 
 *  ✅ 로그인 체크
 *  ✅ 로그인
 *  🔐 로그인 세팅
 *  ✅ 로그아웃
 *  🔓 로그아웃 세팅
 */ 

import React, { createContext, useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import * as auth from '../apis/auth';

export const LoginContext = createContext();
LoginContext.displayName = 'LoginContextName'


const LoginContextProvider = ({ children }) => {

    // 로그인 여부
    const [isLogin, setLogin] = useState(false);

    // 유저 정보
    const [userInfo, setUserInfo] = useState(null);

    const location = useLocation();
    const navigate = useNavigate();

    // ✅ 로그인 체크
    const loginCheck = async () => {
        console.log('loginCheck 실행 중');

        let response
        let userData
        
        if (isLogin || userInfo) {
            return;
        }

        if (location.pathname === '/login' || location.pathname === '/oauth2') {
            return;
        }

        /*
            유저정보 요청
            엑세스 토큰과 리프래시 토큰이 유효하다면 가져옴
        */
        try {
            response = await auth.info();
            userData = response.data;
                
            // 객체를 문자열로 변환하여 체크하는 로직을 추가
            const responseDataAsString = typeof userData === 'string' ? userData : JSON.stringify(userData);
            
            if (responseDataAsString.startsWith('<!DOCTYPE html>')) {
                throw new Error('Unauthorized'); // HTML 응답이므로 인증 실패로 간주
            }
        
        } catch (error) {
            console.log(`로그인 에러 : ${error}`);
            
            const errorStatus = error.response ? error.response.status : '상태 코드 불명';
            alert('로그인 시간이 만료되었습니다. 다시 로그인해주세요.');

            return;
        }

        // ❌ 인증 실패
        if( userData == 'UNAUTHRIZED' || response.status == 401 ) {
            console.error(`accessToken이 유효하지 않습니다.`);
            return
        }

        // 로그인 세팅
        loginSetting(userData)
    }

    // 🔐 로그인 세팅
    const loginSetting = (userData) => {
        const { userId, nickname, role } = userData

        console.log(`userId : ${userId}`);
        console.log(`nickname : ${nickname}`);
        console.log(`role : ${role}`);

        // 👩‍💼🔐 로그인 여부 : true
        setLogin(true)
        
        // 👩‍💼✅ 유저정보 세팅
        const updatedUserInfo = {userId, nickname, role}
        setUserInfo(updatedUserInfo)

    }

    // 🔓 로그아웃
    const logout = (force=false) => {

        if( force ) {
            // 로그아웃 세팅
            logoutSetting()
        
            navigate("/login")
            return
        }        

        if (window.confirm("로그아웃하시겠습니까?")) {
            console.log('로그아웃 진행 중');
            // 로그아웃 세팅
            logoutSetting();
        
            // 메인 페이지로 이동
            navigate("/login");
        }

    }

    // 로그아웃 세팅
    const logoutSetting = async () => {
        console.log(' logoutSetting 동작중');
        localStorage.removeItem('Authorization'); // 엑세스 토큰 제거
        await auth.logout();                      // 쿠키에 있는 리프래시 토큰 제거
        setLogin(false)                           // 로그인 여부 : false
        setUserInfo(null)                         // 유저 정보 초기화

    }

    useEffect( () => {
    
        // 로그인 체크
        loginCheck()
    
    }, [location.pathname])

    return ( 
        <LoginContext.Provider value={ {isLogin, userInfo, loginCheck, logout} }>
            {children}
        </LoginContext.Provider>
    )
}

export default LoginContextProvider