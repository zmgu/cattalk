/*
 *  로그인 
 *  ✅ 로그인 체크
 *  ✅ 로그인
 *  ✅ 로그아웃
 *  
 *  🔐 로그인 세팅
 *  🔓 로그아웃 세팅
 */ 

import React, { createContext, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import * as auth from '../apis/auth';

export const LoginContext = createContext();
LoginContext.displayName = 'LoginContextName'


const LoginContextProvider = ({ children }) => {

    // 로그인 여부
    const [isLogin, setLogin] = useState(false);

    // 유저 정보
    const [userInfo, setUserInfo] = useState(null)

    const location = useLocation();
    /* 
        ✅ 로그인 체크
    */
    const loginCheck = async () => {

        let response
        let userData
        
        if (location.pathname === '/login') {
            return;
        }

        /*
            유저정보 요청
            엑세스 토큰과 리프래시 토큰이 유효하다면 가져옴
        */

        try {
            response = await auth.info();
            console.log('트라이 캐치문');
            if (userData.startsWith('<!DOCTYPE html>')) {
                throw new Error('Unauthorized'); // HTML 응답이므로 인증 실패로 간주
            }

        } catch (error) {
            console.log(`로그인 에러 : ${error}`);
            console.log(`로그인 에러 상태 : ${response.status}`);
            window.location.href = '/login';
            return;
        }

        userData = response.data
        console.log(`userData : ${userData}`); // 추후 지울 부분

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

        const { name, nickname, role } = userData

        console.log(`username : ${name}`);
        console.log(`nickname : ${nickname}`);
        console.log(`role : ${role}`);

        // 👩‍💼🔐 로그인 여부 : true
        setLogin(true)
        
        // 👩‍💼✅ 유저정보 세팅
        const updatedUserInfo = {name, nickname, role}
        setUserInfo(updatedUserInfo)

        // 👮‍♀️✅ 권한정보 세팅
        const updatedRoles = { isUser : false, isAmdin : false }
        

            if( role == 'ROLE_USER' ) updatedRoles.isUser = true;
            if( role == 'ROLE_ADMIN' ) updatedRoles.isAdmin = true;

        // setRoles(updatedRoles)
    }

    useEffect( () => {
    
        // 로그인 체크
        loginCheck()
    
    }, [location.pathname])

    return ( 
        <LoginContext.Provider value={ {isLogin, userInfo, loginCheck} }>
            {children}
        </LoginContext.Provider>
    )
}

export default LoginContextProvider