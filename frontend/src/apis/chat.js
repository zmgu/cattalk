import privateApi from './privateApi';

// 채팅방 생성
export const createChatRoom = (myUserId, myNickname, friendUserId, friendNickname) => 
    privateApi.post(`/chat/room`, { myUserId, myNickname,friendUserId, friendNickname });


// 채팅방 찾기
export const findChatRoom = (myUserId, friendUserId) => 
    privateApi.get(`/chat/room`, { myUserId, friendUserId });
