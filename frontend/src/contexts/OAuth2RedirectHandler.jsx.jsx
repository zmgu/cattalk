// 소셜로그인 리다이렉트를 위한 컴포넌트
import React, { useEffect } from 'react';
import { useHistory } from 'react-router-dom';
import { createAccessToken } from '../apis/auth';

const OAuth2RedirectHandler = () => {
    const history = useHistory();

    useEffect(() => {
        refreshTokenAndRedirect();
    }, []);

    const refreshTokenAndRedirect = async () => {
        try {
            const response = await createAccessToken();
            localStorage.setItem('accessToken', response.data.accessToken);
            history.push('/');
        } catch (error) {
            // alert 메시지 (다시 로그인 해주세요 같은 걸로 변경 예정)
            console.error('엑세스 토큰 재발급 실패', error);
            history.push('/login');
        }
    };

    return (
        <div>로그인 처리 중...</div>
    );
};

export default OAuth2RedirectHandler;
