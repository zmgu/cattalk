// 소셜로그인 리다이렉트를 위한 컴포넌트
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
            console.log('publicApi 시작')
            const response = await publicApi.post('/auth/reissue', {}, { withCredentials: true });
            localStorage.setItem('accessToken', response.data.accessToken);
            navigate('/');
        } catch (error) {
            // alert 메시지 (다시 로그인 해주세요 같은 걸로 변경 예정)
            console.error('publicApi에서 에러 발생', error);
            navigate('/login');
        }
    };

    return (
        <div>로그인 처리 중...</div>
    );
};

export default OAuth2RedirectHandler;
