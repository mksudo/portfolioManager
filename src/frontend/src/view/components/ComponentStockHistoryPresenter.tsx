import {ComponentStockHistory, IComponentStockHistoryProp} from "./ComponentStockHistory";
import React from "react";
import {ComponentStockHistoryDataCell} from "./ComponentStockHistoryDataCell";

type IComponentStockHistoryPresenterProps = {
    histories: IComponentStockHistoryProp[]
}


export class ComponentStockHistoryPresenter
    extends React.Component<IComponentStockHistoryPresenterProps> {

    render() {
        return (
            <ul className={"history-table"}>
                <li className={"history-table-header"}>
                    <ComponentStockHistoryDataCell data={"Symbol"}/>

                    <ComponentStockHistoryDataCell data={"Time"}/>

                    <ComponentStockHistoryDataCell data={"Low"}/>

                    <ComponentStockHistoryDataCell data={"High"}/>

                    <ComponentStockHistoryDataCell data={"Open"}/>

                    <ComponentStockHistoryDataCell data={"Close"}/>

                    <ComponentStockHistoryDataCell data={"Volume"}/>
                </li>
                {
                    this.props.histories.map(
                        history => <ComponentStockHistory
                            time={history.time}
                            symbol={history.symbol}
                            low={history.low}
                            high={history.high}
                            open={history.open}
                            close={history.close}
                            volume={history.volume}
                        />
                    )
                }
            </ul>
        )
    }
}