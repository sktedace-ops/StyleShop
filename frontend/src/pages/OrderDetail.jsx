import {useEffect,useState} from "react";
import {useNavigate,useParams,Link} from "react-router-dom";
import {request,imageUrl} from "../api";
import PageActions from "../components/PageActions";

function money(v){ return Number(v||0).toLocaleString("zh-TW"); }

export default function OrderDetail(){
  const {id}=useParams();
  const nav=useNavigate();
  const [o,setO]=useState(null);
  const [msg,setMsg]=useState("");

  useEffect(()=>{
    request(`/api/orders/${id}`)
      .then(setO)
      .catch(e=>{
        if(e.status===401) nav("/login");
        else if(e.status===404) nav("/orders");
        else setMsg(e.message||"訂單載入失敗");
      });
  },[id]);

  async function cancelOrder(){
    if(!window.confirm("確定要取消這筆訂單嗎？")) return;
    try{
      await request(`/api/orders/${id}/cancel`,{method:"PUT"});
      const fresh=await request(`/api/orders/${id}`);
      setO(fresh);
      setMsg("");
    }catch(e){
      setMsg(e.message||"取消訂單失敗");
    }
  }

  if(msg) return <main><PageActions/><p className="error">{msg}</p></main>;
  if(!o) return <main><PageActions/><div className="panel">訂單載入中...</div></main>;

  const subtotal=Number(o.subtotal ?? o.totalAmount ?? 0);
  const shippingFee=Number(o.shippingFee ?? 0);
  const discountAmount=Number(o.discountAmount ?? 0);
  const finalAmount=Number(o.finalAmount ?? o.totalAmount ?? subtotal);

  return <main>
    <PageActions/>
    <div className="heading">
      <div>
        <h1>訂單 #{o.id}</h1>
        <p className="muted">{new Date(o.createdAt).toLocaleString("zh-TW")}</p>
      </div>
      <span className="status-badge">{o.status}</span>
    </div>

    <div className="checkout-layout">
      <div className="cartlist">
        {(o.items||[]).map(i=>
          <div className="cartrow" key={i.id}>
            <img src={imageUrl(i.imageUrl)} alt={i.productName||"商品"}/>
            <div className="grow">
              <h3>{i.productName}</h3>
              <p className="muted">NT$ {money(i.price)} × {Number(i.quantity||0)}</p>
            </div>
            <strong>小計 NT$ {money(i.subtotal)}</strong>
          </div>
        )}
      </div>

      <aside className="order-summary-card">
        <h2>訂單總結</h2>
        <div className="summary-line"><span>訂單編號</span><b>#{o.id}</b></div>
        <div className="summary-line"><span>商品件數</span><b>{Number(o.totalQuantity||0)} 件</b></div>
        <div className="summary-line"><span>商品小計</span><b>NT$ {money(subtotal)}</b></div>
        <div className="summary-line"><span>運費</span><b>{shippingFee===0?"免運":`NT$ ${money(shippingFee)}`}</b></div>
        <div className="summary-line"><span>折扣</span><b>- NT$ {money(discountAmount)}</b></div>
        <div className="summary-divider"/>
        <div className="summary-line summary-total"><span>實付金額</span><b>NT$ {money(finalAmount)}</b></div>
        <div className="summary-line"><span>訂單狀態</span><b>{o.status}</b></div>
        {o.status==="訂單成立"&&<button className="danger-outline summary-cancel-order" onClick={cancelOrder}>取消訂單</button>}
        {o.status==="已取消"&&<p className="cancelled-note">此訂單已取消</p>}
        <Link className="summary-back-link" to="/orders">返回訂單紀錄</Link>
      </aside>
    </div>
  </main>
}
