import React, { createContext, useEffect, useState } from 'react';
import api from '../apis/api';
import Cookies from 'js-cookie';
import * as auth from '../apis/auth';
import { useNavigate } from 'react-router-dom';
import * as Swal from '../apis/alert';

export const LoginContext = createContext();
LoginContext.displayName = 'LoginContextName'

/**
 *  로그인 
 *  ✅ 로그인 체크
 *  ✅ 로그인
 *  ✅ 로그아웃
 *  
 *  🔐 로그인 세팅
 *  🔓 로그아웃 세팅
*/
const LoginContextProvider = ({ children }) => {
    /*
        상태
        - 로그인 여부
        - 유저 정보
        - 권한 정보
        - 아이디 저장
    */
   /* -----------------------[State]-------------------------- */
    // 로그인 여부
    const [isLogin, setLogin] = useState(false);

    // 유저 정보
    const [userInfo, setUserInfo] = useState(null)

    // 아이디 저장
    const [remberUserId, setRemberUserId] = useState()
    /* -------------------------------------------------------- */

    // 페이지 이동
    const navigate = useNavigate()


    /* 
        💍✅ 로그인 체크
        - 쿠키에 jwt 가 있는지 확인
        - jwt 로 사용자 정보를 요청
    */
    const loginCheck = async () => {

        // 👩‍💼 사용자 정보 요청
        let response
        let userData
        const authroization = headers.authorization
        const accessToken = authroization.replace("Bearer ", "")  // 💍 JWT

        try {
            response = await auth.info()

        } catch (error) {
            console.log(`error : ${error}`);
            console.log(`status : ${response.status}`);
            return;
        }

        userData = response.data
        console.log(`userData : ${userData}`);

        // ❌ 인증 실패
        if( userData == 'UNAUTHRIZED' || response.status == 401 ) {
            console.error(`accessToken (jwt) 이 만료되었거나 인증에 실패하였습니다.`);
            return
        }

        // ✅ 인증 성공
        console.log(`accessToek (jwt) 로큰으로 사용자 인증정보 요청 성공!`);

        // 로그인 세팅
        loginSetting(userData)
    }

    // 🔐 로그인 
    const login = async (username, password) => {

        console.log(`username : ${username}`);
        console.log(`password : ${password}`);

        try {
            const response = await auth.login(username, password)
            const data = response.data
            const status = response.status
            const headers = response.headers
            const authroization = headers.authorization
            const accessToken = authroization.replace("Bearer ", "")  // 💍 JWT

            console.log(`data : ${data}`);
            console.log(`status : ${status}`);
            console.log(`headers : ${headers}`);
            console.log(`jwt : ${accessToken}`);

            // ✅ 로그인 성공
            if( status === 200 ) {
                // 💍 ➡ 🍪 쿠키에 accessToken(jwt) 저장
                Cookies.set("accessToken", accessToken)

                // 로그인 체크 ( /users/{userId}   <---  userData )
                loginCheck()
                
                // 여기
                // alert(`로그인 성공`)
                Swal.alert(`로그인 성공`, `메인 화면으로 갑니다.`, "success", () => { navigate("/")})

                // 메인 페이지로 이동
                navigate("/")
            }
        } catch (error) {
            // 로그인 실패
            // - 아이디 또는 비밀번호가 일치하지 않습니다.
            Swal.alert("로그인 실패", "아이디 또는 비밀번호가 일치하지 않습니다.", "error" )
        }
        

    }

    // 🔓 로그아웃
    const logout = (force=false) => {

        if( force ) {
            // 로그아웃 세팅
            logoutSetting()
        
            // 페이지 이동 ➡ "/" (메인)
            navigate("/")
            return
        }        

        // const check = window.confirm(`로그아웃하시겠습니까?`)

        Swal.confirm("로그아웃하시겠습니까?", "로그아웃을 진행합니다.", "warning",
                (result) => {
                    if( result.isConfirmed ) {
                        // 로그아웃 세팅
                        logoutSetting()

                        // 메인 페이지로 이동
                        navigate("/")
                    }
                }
            )

        // if( check ) {
        //     // 로그아웃 세팅
        //     logoutSetting()

        //     // 메인 페이지로 이동
        //     navigate("/")
        // }

    }

    // 🔐 로그인 세팅
    // 👩‍💼 userData,  💍 accessToken (jwt)
    const loginSetting = (userData) => {

        const { username, nickname, role } = userData

        console.log(`username : ${username}`);
        console.log(`nickname : ${nickname}`);
        console.log(`role : ${role}`);

        // 👩‍💼🔐 로그인 여부 : true
        setLogin(true)
        
        // 👩‍💼✅ 유저정보 세팅
        const updatedUserInfo = {id, username, role}
        setUserInfo(updatedUserInfo)

        // 👮‍♀️✅ 권한정보 세팅
        const updatedRoles = { isUser : false, isAmdin : false }
        

            if( role == 'ROLE_USER' ) updatedRoles.isUser = true
            if( role == 'ROLE_ADMIN' ) updatedRoles.isAdmin = true

        setRoles(updatedRoles)
    }

    // 로그아웃 세팅
    const logoutSetting = () => {
        // 🚀❌ axios 헤더 초기화
        api.defaults.headers.common.Authorization = undefined;

        // 🍪❌ 쿠키 초기화
        Cookies.remove("accessToken")

        // 🔐❌ 로그인 여부 : false
        setLogin(false)

        // 👩‍💼❌ 유저 정보 초기화
        setUserInfo(null)

        // 👮‍♀️❌ 권한 정보 초기화
        setRoles(null)
    }

    useEffect( () => {
    
        // 로그인 체크
        loginCheck()
    
    }, [])

    return ( 
        <LoginContext.Provider value={ {isLogin, userInfo, roles, login, loginCheck, logout} }>
            {children}
        </LoginContext.Provider>
    )
}

export default LoginContextProvider