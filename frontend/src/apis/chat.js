import privateApi from './privateApi';

// 채팅방 생성
export const createChatRoom = (
      myUserId
    , myNickname
    , friendUserId
    , friendNickname
    ) => privateApi.post(`/chat/rooms`, { 
          myUserId
        , myNickname
        , friendUserId
        , friendNickname 
    });

// 내 채팅방 리스트
export const chatRoomList = (userId) =>
    privateApi.get(`/chat/rooms`, {
        params: {
            userId: userId
        }
    });

// 채팅방 찾기
export const findChatRoom = (myUserId, friendUserId) => 
    privateApi.get(`/chat/rooms/find`, {         
        params: {
            myUserId: myUserId,
            friendUserId: friendUserId
        } 
    });

// 채팅방 이름 찾기
export const findChatRoomName = (roomId, userId) => 
    privateApi.get(`/chat/rooms/${roomId}/name`, {
        params: {
            roomId: roomId,
            userId: userId
        }
    });


// 메시지 불러오기
export const getMessages = (roomId) =>
    privateApi.get(`/chat/rooms/${roomId}/messages`, {
        params: {
            roomId: roomId
        }
    });

// 최신 메시지 읽은 시간 불러오기
export const getLastReadTimes = (userId, roomId) =>
    privateApi.get(`/chat/rooms/${roomId}/times`, {
        params: {
            userId: userId,
            roomId: roomId
        }
    });