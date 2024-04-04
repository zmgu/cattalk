import './FriendsListPage.css'
import FriendsListHeader from "../../../components/friendsList/friendsListHeader/FriendsListHeader";
import FriendsList from "../../../components/friendsList/FriendsList";
import MyProfile from "../../../components/friendsList/myProfile/MyProfile";

const FriendsListPage = () => {
    return (
        <div>
            <FriendsListHeader />
            <MyProfile />
            <hr className="line"/>
            <FriendsList />
        </div>

    );
}

export default FriendsListPage;