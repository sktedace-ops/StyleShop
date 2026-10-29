import {useEffect,useMemo,useState} from "react";
import {Link,useNavigate} from "react-router-dom";
import {request} from "../api";
import PageActions from "../components/PageActions";

function money(v){ return Number(v||0).toLocaleString("zh-TW"); }
function isCancelled(status){ return status==="CANCELLED" || status==="已取消"; }
function statusText(status){ return isCancelled(status)?"已取消":status==="CREATED"?"訂單成立":status; }
function totalQuantity(order){ return (order.items||[]).reduce((sum,item)=>sum+Number(item.quantity||0),0); }

export default function Orders(){
  const [orders,setOrders]=useState([]);
  const [loading,setLoading]=useState(true);
  const [msg,setMsg]=useState("");
  const [tab,setTab]=useState("active");
  const nav=useNavigate();

  useEffect(()=>{
    request("/api/orders")
      .then(data=>setOrders(Array.isArray(data)?data:[]))
      .catch(e=>e.status===401?nav("/login"):setMsg(e.message||"訂單載入失敗"))
      .finally(()=>setLoading(false));
  },[]);

  const activeOrders=useMemo(
    ()=>orders.filter(o=>!isCancelled(o.status)),
    [orders]
  );
  const cancelledOrders=useMemo(
    ()=>orders.filter(o=>isCancelled(o.status)),
    [orders]
  );
  const visibleOrders=tab==="cancelled"?cancelledOrders:activeOrders;

  return <main>
    <PageActions/>

    <div className="heading">
      <div>
        <h1>我的訂單</h1>
        <p className="muted">一般訂單與已取消訂單分開顯示</p>
      </div>
      <button className="cart-page-button" onClick={()=>nav("/cart")}>前往購物車</button>
    </div>

    <div className="order-tabs">
      <button
        className={tab==="active"?"order-tab active":"order-tab"}
        onClick={()=>setTab("active")}
      >
        訂單紀錄 <span>{activeOrders.length}</span>
      </button>
      <button
        className={tab==="cancelled"?"order-tab active":"order-tab"}
        onClick={()=>setTab("cancelled")}
      >
        已取消訂單 <span>{cancelledOrders.length}</span>
      </button>
    </div>

    {msg&&<p className="error">{msg}</p>}
    {loading&&<div className="panel"><p>訂單載入中...</p></div>}

    {!loading&&visibleOrders.length===0&&
      <div className="panel empty-cart">
        <h2>{tab==="cancelled"?"目前沒有已取消訂單":"目前沒有進行中的訂單"}</h2>
        <p>{tab==="cancelled"
          ?"取消訂單後，該筆訂單會移到這個分頁。"
          :"完成購物車結帳後，訂單會顯示在這裡。"}</p>
        {tab==="active"&&
          <button className="primary" onClick={()=>nav("/cart")}>前往購物車</button>}
      </div>
    }

    {!loading&&visibleOrders.length>0&&
      <div className="orders">
        {visibleOrders.map(o=>
          <Link
            className={`ordercard order-summary-list-card ${isCancelled(o.status)?"cancelled-order-card":""}`}
            to={`/orders/${o.id}`}
            key={o.id}
          >
            <div>
              <b className="order-number">訂單 #{o.id}</b>
              <p className="muted">{new Date(o.createdAt).toLocaleString("zh-TW")}</p>
              <span className={isCancelled(o.status)?"status-badge cancelled":"status-badge"}>
                {statusText(o.status)}
              </span>
            </div>
            <div className="ordercard-values">
              <span>商品 {totalQuantity(o)} 件</span>
              <span>商品小計 NT$ {money(o.subtotal ?? o.totalAmount)}</span>
              <strong>實付 NT$ {money(o.finalAmount ?? o.totalAmount)}</strong>
            </div>
          </Link>
        )}
      </div>
    }
  </main>
}
