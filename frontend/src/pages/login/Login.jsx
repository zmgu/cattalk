import React, { useState } from 'react';
import './Login.css';
import { Link } from 'react-router-dom'
import LoginForm from '../../components/login/LoginForm';

const onSocialLogin = (registrationId) => {
    
    window.location.href = `http://localhost:8888/oauth2/authorization/${registrationId}`
}

const Login = () => {
    return (
        <div className='login-container'>
            <div className='login-logo'></div>
            <LoginForm />
            <div className="login-sub-link-box">
                <Link to="" className="login-sub-link">아이디찾기</Link>
                <hr className="separator" />
                <Link to="" className="login-sub-link">비밀번호찾기</Link>
                <hr className="separator" />
                <Link to="" className="login-sub-link">회원가입</Link>
            </div>

            <div className="login-btn-box">
                <p className="sosial-login-p">소셜 계정 로그인</p>
                <ul className="login-logo-ul">
                    <li className="login-logo-li">
                        <button className="naver" onClick={() => onSocialLogin("naver")}></button>
                    </li>
                    <li className="login-logo-li google">
                        <button className="google" onClick={() => onSocialLogin("google")}></button>
                    </li>
                    <li className="login-logo-li kakao">
                        <button className="kakao" onClick={() => onSocialLogin("kakao")}></button>
                        </li>
                </ul>
            </div>
        </div>
    );
}

export default Login;