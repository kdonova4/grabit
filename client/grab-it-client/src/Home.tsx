import React from "react";
import ProductViewer from "./ProductViewer";
import ProductByCategory from "./TestCalls/ProductTestCalls/ProductsByCategory";
import AddressTestContainer from "./TestContainers/AddressTestContainer";
import AppUserTestContainer from "./TestContainers/AppUserTestContainer"
import BidTestContainer from "./TestContainers/BidTestContainer";

const Home: React.FC = () => {


    return(
        <>
        <ProductViewer/>
        <ProductByCategory/>
        <AddressTestContainer/>
        <AppUserTestContainer/>
        <BidTestContainer/>
        </>
    )
}

export default Home;