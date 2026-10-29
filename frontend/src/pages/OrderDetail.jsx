import {useEffect,useState} from "react";
import {useNavigate,useParams,Link} from "react-router-dom";
import {request,imageUrl} from "../api";
import PageActions from "../components/PageActions";

function money(v){ return Number(v||0).toLocaleString("zh-TW"); }
function isCancelled(status){ return status==="CANCELLED" || status==="已取消"; }
function statusText(status){ return isCancelled(status)?"已取消":status==="CREATED"?"訂單成立":status; }

export default function OrderDetail(){
  const {id}=useParams();
  const nav=useNavigate();
  const [o,setO]=useState(null);
  const [msg,setMsg]=useState("");
  const [cancelling,setCancelling]=useState(false);

  async function loadOrder(){
    try{
      const order=await request(`/api/orders/${id}`);
      // 舊訂單尚未保存 imageUrl 時，由商品 API 補回圖片。
      const items=await Promise.all((order.items||[]).map(async item=>{
        if(item.imageUrl) return item;
        try{
          const product=await request(`/api/products/${item.productId}`);
          return {...item,imageUrl:product.imageUrl};
        }catch{
          return item;
        }
      }));
      setO({...order,items});
      setMsg("");
    }catch(e){
      if(e.status===401) nav("/login");
      else if(e.status===404) nav("/orders");
      else setMsg(e.message||"訂單載入失敗");
    }
  }

  useEffect(()=>{ loadOrder(); },[id]);

  async function cancelOrder(){
    if(!window.confirm("確定要取消這筆訂單嗎？")) return;
    try{
      setCancelling(true);
      await request(`/api/orders/${id}/cancel`,{method:"PUT"});
      await loadOrder();
    }catch(e){
      if(e.status===401) nav("/login");
      else setMsg(e.message||"取消訂單失敗");
    }finally{
      setCancelling(false);
    }
  }

  if(msg&&!o) return <main><PageActions/><p className="error">{msg}</p></main>;
  if(!o) return <main><PageActions/><div className="panel">訂單載入中...</div></main>;

  const subtotal=Number(o.subtotal ?? o.totalAmount ?? 0);
  const shippingFee=Number(o.shippingFee ?? 0);
  const discountAmount=Number(o.discountAmount ?? 0);
  const finalAmount=Number(o.finalAmount ?? o.totalAmount ?? subtotal);
  const totalQuantity=(o.items||[]).reduce((sum,item)=>sum+Number(item.quantity||0),0);
  const cancelled=isCancelled(o.status);

  return <main>
    <PageActions/>
    <div className="heading">
      <div>
        <h1>訂單 #{o.id}</h1>
        <p className="muted">{new Date(o.createdAt).toLocaleString("zh-TW")}</p>
      </div>
      <span className={cancelled?"status-badge cancelled":"status-badge"}>{statusText(o.status)}</span>
    </div>

    {msg&&<p className="error">{msg}</p>}
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
        <div className="summary-line"><span>商品件數</span><b>{totalQuantity} 件</b></div>
        <div className="summary-line"><span>商品小計</span><b>NT$ {money(subtotal)}</b></div>
        <div className="summary-line"><span>運費</span><b>{shippingFee===0?"免運":`NT$ ${money(shippingFee)}`}</b></div>
        <div className="summary-line"><span>折扣</span><b>- NT$ {money(discountAmount)}</b></div>
        <div className="summary-divider"/>
        <div className="summary-line summary-total"><span>實付金額</span><b>NT$ {money(finalAmount)}</b></div>
        <div className="summary-line"><span>訂單狀態</span><b>{statusText(o.status)}</b></div>
        <Link className="summary-back-link" to="/orders">返回訂單紀錄</Link>
        {!cancelled&&<button className="danger-outline summary-cancel-order" disabled={cancelling} onClick={cancelOrder}>{cancelling?"取消處理中...":"取消訂單"}</button>}
        {cancelled&&<p className="cancelled-note">此訂單已取消</p>}
      </aside>
    </div>
  </main>;
}
