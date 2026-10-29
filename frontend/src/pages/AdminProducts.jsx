import {useEffect,useState} from "react";
import {useNavigate} from "react-router-dom";
import {request,imageUrl} from "../api";
import PageActions from "../components/PageActions";

const empty={id:null,name:"",price:"",imageUrl:"",productUrl:"",description:"",categoryId:""};

export default function AdminProducts(){
  const nav=useNavigate();
  const [products,setProducts]=useState([]);
  const [categories,setCategories]=useState([]);
  const [form,setForm]=useState(empty);
  const [msg,setMsg]=useState("");
  const [error,setError]=useState("");
  const [loading,setLoading]=useState(true);

  async function load(){
    try{
      setLoading(true);
      setError("");
      const [p,c]=await Promise.all([
        request("/api/admin/products"),
        request("/api/categories")
      ]);
      setProducts(Array.isArray(p)?p:[]);
      setCategories(Array.isArray(c)?c:[]);
    }catch(e){
      if(e.status===401) nav("/login");
      else setError(e.message||"商品管理資料載入失敗");
    }finally{
      setLoading(false);
    }
  }

  useEffect(()=>{load()},[]);

  async function save(e){
    e.preventDefault();
    setMsg("");
    setError("");
    try{
      await request("/api/admin/products",{
        method:"POST",
        body:JSON.stringify({
          ...form,
          price:Number(form.price||0),
          categoryId:form.categoryId||null
        })
      });
      setForm(empty);
      setMsg(form.id?"商品修改成功":"商品新增成功");
      await load();
    }catch(e){
      setError(e.message||"商品儲存失敗");
    }
  }

  async function removeProduct(id){
    if(!window.confirm("確定要移除這個商品嗎？")) return;
    setMsg("");
    setError("");
    try{
      await request(`/api/admin/products/${id}`,{method:"DELETE"});
      setMsg("商品已移除");
      if(String(form.id)===String(id)) setForm(empty);
      await load();
    }catch(e){
      setError(e.message||"商品移除失敗");
    }
  }

  function editProduct(p){
    setMsg("");
    setError("");
    setForm({
      id:p.id??null,
      name:p.name??"",
      price:p.price??"",
      imageUrl:p.imageUrl??"",
      productUrl:p.productUrl??"",
      description:p.description??"",
      categoryId:p.categoryId??""
    });
    window.scrollTo({top:0,behavior:"smooth"});
  }

  return <main><PageActions/>
    <div className="heading">
      <div>
        <h1>商品管理</h1>
        <p>使用 MyBatis Mapper XML 新增、修改、查詢與移除商品</p>
      </div>
      <span>共 {products.length} 項商品</span>
    </div>

    <div className="report-actions">
      <a className="report-button" href="http://localhost:8080/api/reports/products.pdf"
        target="_blank" rel="noreferrer">📄 商品清單 PDF</a>
      <a className="report-button" href="http://localhost:8080/api/reports/login-history.pdf"
        target="_blank" rel="noreferrer">🔐 登入紀錄管理</a>
    </div>

    {msg&&<p className="success-msg">{msg}</p>}
    {error&&<p className="error">{error}</p>}

    <div className="adminlayout">
      <form className="panel admin-form" onSubmit={save}>
        <h2>{form.id?"修改商品":"登記商品"}</h2>

        <label>商品名稱
          <input required value={form.name}
            onChange={e=>setForm({...form,name:e.target.value})}/>
        </label>

        <label>價格
          <input required min="0" type="number" value={form.price}
            onChange={e=>setForm({...form,price:e.target.value})}/>
        </label>

        <label>種類
          <select value={form.categoryId??""}
            onChange={e=>setForm({...form,categoryId:e.target.value})}>
            <option value="">未分類</option>
            {categories.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </label>

        <label>圖片路徑
          <input placeholder="/images/example.svg" value={form.imageUrl??""}
            onChange={e=>setForm({...form,imageUrl:e.target.value})}/>
        </label>

        <label>商品網址
          <input value={form.productUrl??""}
            onChange={e=>setForm({...form,productUrl:e.target.value})}/>
        </label>

        <label>商品描述
          <textarea value={form.description??""}
            onChange={e=>setForm({...form,description:e.target.value})}/>
        </label>

        <button className="primary" type="submit">{form.id?"儲存修改":"新增商品"}</button>
        {form.id&&<button type="button" onClick={()=>setForm(empty)}>取消修改</button>}
      </form>

      <section>
        {loading&&<div className="panel"><p>商品資料載入中...</p></div>}

        {!loading&&!error&&products.length===0&&
          <div className="panel empty-admin">
            <h2>目前沒有商品</h2>
            <p>可以使用左側的「登記商品」表單新增第一項商品。</p>
          </div>
        }

        {!loading&&products.length>0&&
          <div className="adminproducts">
            {products.map(p=>{
              const price=Number(p.price||0);
              return <div className="adminproduct" key={p.id}>
                <img src={imageUrl(p.imageUrl)} alt={p.name||"商品"}/>
                <div className="grow">
                  <b>{p.name||"未命名商品"}</b>
                  <p>{p.categoryName||"未分類"} · NT$ {price.toLocaleString()}</p>
                  <small>商品編號：{p.id}</small>
                </div>
                <button onClick={()=>editProduct(p)}>修改</button>
                <button onClick={()=>removeProduct(p.id)}>移除</button>
              </div>
            })}
          </div>
        }
      </section>
    </div>
  </main>;
}
