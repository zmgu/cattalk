import './RoomListConfig.css'

const RoomListConfig = () => {
    const relatedItems = ['아이템 1', '아이템 2', '아이템 3'];

    return (
        <ul>
            {relatedItems.map((item, index) => (
            <li key={index}>{item}</li>
            ))}
        </ul>
        );
    };

export default RoomListConfig;