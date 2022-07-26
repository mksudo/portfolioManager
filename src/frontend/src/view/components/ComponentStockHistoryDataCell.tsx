import React, {ReactNode} from "react";

type IDataCellProp<T> = {
    data: T
}

export class ComponentStockHistoryDataCell<T extends ReactNode> extends React.Component<IDataCellProp<T>> {
    render() {
        return (
            <div className={"cell"}>
                <span className={"cell-content-wrapper"}>
                    {this.props.data}
                </span>
            </div>
        )
    }
}