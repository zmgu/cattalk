import React, { useState } from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faLock } from '@fortawesome/free-solid-svg-icons';
import './LoginForm.css'

const LoginForm = () => {

    const [email, setEmail] = useState('');
    const [password, sePassword] = useState('');

    return (
        <div className="login-form">
            <form method="">
                <div className="input-box">
                    <div className="input-box-icon">
                    <FontAwesomeIcon icon={faUser} className='login-icon'/>
                    </div>
                    <input type='text'
                    value={email}
                    onChange={(event) => (
                        setEmail(event.target.value)
                    )} 
                    placeholder='이메일'/>
                </div>

                <div className="input-box">
                    <div className="input-box-icon">
                    <FontAwesomeIcon icon={faLock} className='login-icon'/>
                    </div>
                    <input type='password'
                    value={password}
                    onChange={(event) => (
                        sePassword(event.target.value)
                    )} 
                    placeholder='비밀번호'/>
                </div>
                <button type="submit" className="submitBtn">
                    <p>로그인</p>
                </button>
            </form>
        </div>
    );
}

export default LoginForm;