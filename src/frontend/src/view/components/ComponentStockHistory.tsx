import React, {ReactNode} from "react";
import {ComponentStockHistoryDataCell} from "./ComponentStockHistoryDataCell";

export type IComponentStockHistoryProp = {
    time: string
    symbol: string
    low: number
    high: number
    open: number
    close: number
    volume: number
}


export class ComponentStockHistory extends React.Component<IComponentStockHistoryProp> {

    render() {
        return (
            <li className={"history-column"}>
                <ComponentStockHistoryDataCell data={this.props.symbol}/>

                <ComponentStockHistoryDataCell data={this.props.time}/>

                <ComponentStockHistoryDataCell data={this.props.low}/>

                <ComponentStockHistoryDataCell data={this.props.high}/>

                <ComponentStockHistoryDataCell data={this.props.open}/>

                <ComponentStockHistoryDataCell data={this.props.close}/>

                <ComponentStockHistoryDataCell data={this.props.volume}/>
            </li>
        )
    }
}