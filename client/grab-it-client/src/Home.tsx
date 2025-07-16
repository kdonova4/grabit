import React from "react";
import ProductByCategory from "./TestCalls/ProductTestCalls/ProductsByCategory";
import AddressTestContainer from "./TestContainers/AddressTestContainer";
import AppUserTestContainer from "./TestContainers/AppUserTestContainer"
import BidTestContainer from "./TestContainers/BidTestContainer";

const Home: React.FC = () => {


    return(
        <>
        <ProductByCategory/>
        <AddressTestContainer/>
        <AppUserTestContainer/>
        <BidTestContainer/>
        </>
    )
}

export default Home;