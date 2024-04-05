import privateApi from './privateApi';

// 채팅방 생성
export const createChatRoom = (myUserId, friendUserId, friendNickname) => 
    privateApi.post(`/chat/room`, { myUserId, friendUserId, friendNickname });
