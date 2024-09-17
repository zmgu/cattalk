// 소셜로그인 리다이렉트를 위한 컴포넌트
import './OAuth2RedirectHandler.css'
import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import publicApi from '../apis/publicApi';

const OAuth2RedirectHandler = () => {
    const navigate = useNavigate();

    useEffect(() => {
        refreshTokenAndRedirect();
    }, []);

    const refreshTokenAndRedirect = async () => {
        try {
            const response = await publicApi.post('/auth/reissue', {}, { withCredentials: true });

            const accessToken = response.headers['authorization'];        
            localStorage.setItem('Authorization', accessToken);

            setTimeout(() => {
                navigate('/');
            }, 2000);
        } catch (error) {
            // alert 메시지 (다시 로그인 해주세요 같은 걸로 변경 예정)
            console.error('publicApi에서 에러 발생', error);
            navigate('/login');
        }
    };

    return (
        <div className='handler-container'>
            <div className='handler-header'></div>
            <div className='loading-img'>　　</div>
            <div className='loading-message'>로그인 중...</div>
            <div className='handler-bottom'></div>
        </div>
    );
};

export default OAuth2RedirectHandler;
