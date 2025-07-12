import Login from "../TestCalls/AppUserTestsCalls/Login";
import Register from "../TestCalls/AppUserTestsCalls/Register";
import Verify from "../TestCalls/AppUserTestsCalls/Verify";

const AppUserTestContainer: React.FC = () => {

    return (
        <>
        <Login/>
        <Register/>
        <Verify/>
        </>
    )
}

export default AppUserTestContainer;